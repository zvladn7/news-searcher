import React, { useCallback, useState } from 'react';
import { useQueryParams } from '../../hooks/useQueryParams';
import Header from '../../components/header';
import { useLanguage } from '../../hooks/useLanguage';
import { useQueriesStorage } from '../../hooks/useQueriesStorage';
import {
  PAGE_QUERY_PARAMETER,
  SEARCH_PAGE_PATH,
  SEARCH_VALUE_QUERY_PARAMETER,
  TAB_QUERY_PARAMETER,
} from '../../constants/url';
import BlockResultSearch from '../../components/ui/blockResultSearch';
import {
  loaderText,
  similarResultsText,
} from '../../constants/language';
import SimilarQueries from '../../components/ui/similarQueries';
import { useWindowSize } from '../../hooks/useWindowSize';
import Loader from '../../components/ui/loader';
import ReactPaginate from 'react-paginate';
import ArrowLeft from '../../assets/svg/arrowLeft';
import ArrowRight from '../../assets/svg/arrowRight';
import ImageBlock from '../../components/ui/imageBlock';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Scrollbars } from 'react-custom-scrollbars';
import SearchCount from '../../components/ui/searchCount';
import NotFound from '../../components/notFound';
import { getBreaksRules } from '../../utils/queryRules';
import BreakRules from '../../components/breakRules';
import { useSearchQueryResultText } from '../../services/searchQueryService';
import { useSimilarQueryResult } from '../../services/similarQueryService';
import { useSearchQueryResultImage } from '../../services/imagesService';

const RESULTS_TEXT_ON_PAGE = 10;

const Search = () => {
  const [queryParams, navigateWithQueryParams] = useQueryParams();
  const windowSize = useWindowSize();
  const {viewType} = windowSize;
  const [queriesHistory, addQueryIntoHistory, deleteQueryFromHistory] = useQueriesStorage();

  const [inputSearchValue, setInputSearchValue] = useState(queryParams.get(SEARCH_VALUE_QUERY_PARAMETER).trim() || '');
  const [language, changeLanguage] = useLanguage();
  const [tab, setTab] = useState(Number(queryParams.get(TAB_QUERY_PARAMETER)) || 0);
  const [page, setPage] = useState(Number(queryParams.get(PAGE_QUERY_PARAMETER)) || 0);
  const [breaksRules, setBreaksRules] = useState([]);

  const [
    resultText,
    resultTextCount,
    resultTextLoading,
    resultTextError,
    resultTextSendRequest,
  ] = useSearchQueryResultText(page, inputSearchValue);

  const [
    similarQuery,
    similarQueryLoading,
    similarQueryError,
    similarQuerySendRequest,
  ] = useSimilarQueryResult(inputSearchValue);

  const [
    resultImage,
    resultImageCount,
    resultImageLoading,
    resultImageError,
    resultImageSendRequest
  ] = useSearchQueryResultImage(inputSearchValue);

  // getBreaksRules(inputSearchValue);

  const handleOnChangeInputSearchValue = (value) => setInputSearchValue(value);

  const handleOnSearch = useCallback((query) => {
    const currentQuery = query.trim();
    addQueryIntoHistory(currentQuery);
    setInputSearchValue(currentQuery);

    // const newBreaksRules = getBreaksRules(query); //TODO: исправить правила запроса
    const newBreaksRules = [];
    if (newBreaksRules.length > 0) {
      setBreaksRules(newBreaksRules);
    } else {
      setPage(0);
      navigateWithQueryParams(SEARCH_PAGE_PATH, {
        [SEARCH_VALUE_QUERY_PARAMETER]: currentQuery,
        [TAB_QUERY_PARAMETER]: tab,
        [PAGE_QUERY_PARAMETER]: 0,
      }, true);
      resultTextSendRequest(0, currentQuery);
      similarQuerySendRequest(currentQuery);
      resultImageSendRequest(currentQuery);
    }
  }, [addQueryIntoHistory, tab]);

  const handleOnChangeTab = useCallback((tabId) => {
    setTab(tabId);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: inputSearchValue.trim(),
      [TAB_QUERY_PARAMETER]: tabId,
      [PAGE_QUERY_PARAMETER]: page,
    }, true);
  }, [inputSearchValue, page]);

  const handleOnPageChange = useCallback(({selected}) => {
    setPage(selected);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: inputSearchValue.trim(),
      [TAB_QUERY_PARAMETER]: tab,
      [PAGE_QUERY_PARAMETER]: selected,
    }, true);

    if (selected !== page) {
      resultTextSendRequest(selected, inputSearchValue.trim());
    }
  }, [inputSearchValue, tab, page]);

  return (
    <div className="search">
      <div className="search__header">
        <Header
          searchValue={inputSearchValue}
          onChangeSearchValue={handleOnChangeInputSearchValue}
          queriesHistory={queriesHistory}
          deleteQueryFromHistory={deleteQueryFromHistory}
          onSearch={handleOnSearch}
          language={language}
          changeLanguage={changeLanguage}
          tabSelected={tab}
          onChangeTab={handleOnChangeTab}
        />
      </div>
      <div className="search__content-wrapper">
        {tab === 0 && (
          <Scrollbars
            autoHide
          >
            <div className="search__content">
              {breaksRules.length > 0 ? (
                <div className="search__content-item">
                  <BreakRules
                    breakRules={breaksRules}
                    searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''}
                    language={language}
                  />
                </div>
              ) : (
                resultTextLoading ? (
                    <Loader text={loaderText[language]} />
                  ) :
                  !!resultText && resultText.length > 0 && !resultTextError ? (
                    <>
                      <div className="search__content-item">
                        <SearchCount language={language} resultCount={resultTextCount} />
                      </div>
                      <div className="search__content-item">
                        <BlockResultSearch
                          title={resultText[0].title}
                          link={resultText[0].link}
                        />
                      </div>
                      {!!similarQuery && similarQuery.length > 0 && !similarQueryError && page === 0 && (
                        <div className="search__content-item">
                          <SimilarQueries
                            title={similarResultsText[language]}
                            queries={similarQuery}
                            onClickItem={handleOnSearch}
                            viewType={viewType}
                          />
                        </div>
                      )}
                      {resultText.slice(1).map((item) => (
                        <div className="search__content-item" key={`search__content-item-${item.id}`}>
                          <BlockResultSearch
                            title={item.title}
                            link={item.link}
                          />
                        </div>
                      ))}
                      {resultTextCount > RESULTS_TEXT_ON_PAGE && (
                        <ReactPaginate
                          pageCount={Math.ceil(resultTextCount / resultText.length)}
                          pageRangeDisplayed={7}
                          marginPagesDisplayed={0}
                          previousLabel={<ArrowLeft />}
                          nextLabel={<ArrowRight />}
                          breakLabel=""
                          onPageChange={handleOnPageChange}
                          initialPage={page}
                          className="search__content-paginate"
                          pageClassName="search__content-paginate-li"
                          activeClassName="search__content-paginate-li search__content-paginate-li--selected"
                          previousClassName="search__content-paginate-button"
                          nextClassName="search__content-paginate-button"
                        />
                      )}
                    </>
                  ) : (
                    <>
                      <div className="search__content-item">
                        <SearchCount language={language} resultCount={resultTextCount} />
                      </div>
                      <div className="search__content-item">
                        <NotFound
                          language={language} searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''}
                        />
                      </div>
                    </>
                  )
              )}
            </div>
          </Scrollbars>
        )}
        {tab === 1 && (
          <Scrollbars
            autoHide
            renderView={(props) => <div {...props} id="search__content-scrollbar" />}
          >
            <div className="search__content">
              {breaksRules.length > 0 ? (
                <div className="search__content-item">
                  <BreakRules
                    breakRules={breaksRules}
                    searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''}
                    language={language}
                  />
                </div>
              ) : (
                !!resultImage && resultImage.length > 0 ? (
                  <InfiniteScroll
                    hasMore={true}
                    next={() => resultImageSendRequest(inputSearchValue)}
                    loader={
                      <div className="search__content-images-loader">
                        <Loader text={loaderText[language]} />
                      </div>
                    }
                    dataLength={resultImage.length}
                    className="search__content-images"
                    scrollableTarget="search__content-scrollbar"
                  >
                    {resultImage.map((image) =>
                      <div className="search__content-images-item" key={`search__content-images-item-${image.id}`}>
                        <ImageBlock title={image.title} link={image.link} imageUrl={image.imageUrl} />
                      </div>,
                    )}
                  </InfiniteScroll>
                ) : (
                  <div className="search__content-item">
                    <NotFound language={language} searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''} />
                  </div>
                )
              )}
            </div>
          </Scrollbars>
        )}
      </div>
    </div>
  );
};

export default Search;
