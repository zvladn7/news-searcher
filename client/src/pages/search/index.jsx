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
import { resultsText } from '../../mocks/resultsText';
import { similarQueries } from '../../mocks/similarQueries';
import BlockResultSearch from '../../components/ui/blockResultSearch';
import {
  aboutResultsTextFirstPart,
  aboutResultsTextSecondPart,
  loaderText,
  similarResultsText,
} from '../../constants/language';
import SimilarQueries from '../../components/ui/similarQueries';
import { useWindowSize } from '../../hooks/useWindowSize';
import { useApi } from '../../hooks/useApi';
import Loader from '../../components/ui/loader';
import ReactPaginate from 'react-paginate';
import ArrowLeft from '../../assets/svg/arrowLeft';
import ArrowRight from '../../assets/svg/arrowRight';
import { resultsImage } from '../../mocks/resultsImage';
import ImageBlock from '../../components/ui/imageBlock';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Scrollbars } from 'react-custom-scrollbars';
import SearchCount from '../../components/ui/searchCount';
import NotFound from '../../components/notFound';

const Search = () => {
  const [queryParams, navigateWithQueryParams] = useQueryParams();
  const windowSize = useWindowSize();
  const {viewType} = windowSize;
  const [inputSearchValue, setInputSearchValue] = useState(queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || '');
  const [queriesHistory, addQueryIntoHistory, deleteQueryFromHistory] = useQueriesStorage();
  const [language, changeLanguage] = useLanguage();
  const [tab, setTab] = useState(Number(queryParams.get(TAB_QUERY_PARAMETER)) || 0);
  const [page, setPage] = useState(Number(queryParams.get(PAGE_QUERY_PARAMETER)) || 0);

  // TODO: connect api for text results
  const {
    response: resultResponse,
    isLoading: resultLoading,
    error: resultError,
    sendRequest: resultSendRequest,
  } = useApi({
    url: `/search/${page}`,
    method: 'get',
    headers: JSON.stringify({}),
    body: JSON.stringify({}),

  });

  const [result, setResult] = useState(resultsText.searchItems);
  const [resultCount, setResultCount] = useState(resultsText.totalCount);
  const [similarQuery, setSimilarQuery] = useState(similarQueries);

  const [resultImage, setResultImage] = useState(resultsImage);

  const handleOnChangeInputSearchValue = (value) => setInputSearchValue(value);

  const handleOnSearch = useCallback((query) => {
    addQueryIntoHistory(query);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: query,
      [TAB_QUERY_PARAMETER]: tab,
      [PAGE_QUERY_PARAMETER]: 0,
    });
    setInputSearchValue(query);
  }, [addQueryIntoHistory, tab]);

  const handleOnChangeTab = useCallback((tabId) => {
    setTab(tabId);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: inputSearchValue,
      [TAB_QUERY_PARAMETER]: tabId,
      [PAGE_QUERY_PARAMETER]: page,
    });
  }, [inputSearchValue, page]);

  const handleOnPageChange = useCallback(({selected}) => {
    setPage(selected);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: inputSearchValue,
      [TAB_QUERY_PARAMETER]: tab,
      [PAGE_QUERY_PARAMETER]: selected,
    });
  }, [inputSearchValue, tab]);

  const handleOnNextImagePage = useCallback(() => {
    setTimeout(() => setResultImage([...resultImage, ...resultsImage]), 5000);
  }, [resultImage]);

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
              {!!result && result.length > 0 ?
                resultLoading ? (
                  <Loader text={loaderText[language]} />
                ) : (
                  <>
                    <div className="search__content-item">
                      <SearchCount language={language} resultCount={resultCount} />
                    </div>
                    <div className="search__content-item">
                      <BlockResultSearch
                        title={result[0].title}
                        link={result[0].link}
                      />
                    </div>
                    {similarQuery && page === 0 && (
                      <div className="search__content-item">
                        <SimilarQueries
                          title={similarResultsText[language]}
                          queries={similarQuery}
                          onClickItem={handleOnSearch}
                          viewType={viewType}
                        />
                      </div>
                    )}
                    {result.slice(1).map((item) => (
                      <div className="search__content-item" key={`search__content-item-${item.id}`}>
                        <BlockResultSearch
                          title={item.title}
                          link={item.link}
                        />
                      </div>
                    ))}
                    <ReactPaginate
                      pageCount={Math.ceil(resultCount / result.length)}
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
                  </>
                ) : (
                  <>
                    <div className="search__content-item">
                      <SearchCount language={language} resultCount={resultCount} />
                    </div>
                    <div className="search__content-item">
                      <NotFound language={language} searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''} />
                    </div>
                  </>
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
              {!!resultImage && resultImage.length > 0 ? (
                <InfiniteScroll
                  hasMore={true}
                  next={handleOnNextImagePage}
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
                      <ImageBlock title={image.title} link={image.link} imageUrl={image.images} />
                    </div>,
                  )}
                </InfiniteScroll>
              ) : (
                <div className="search__content-item">
                  <NotFound language={language} searchQuery={queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || ''} />
                </div>
              )}
            </div>
          </Scrollbars>
        )}
      </div>
    </div>
  );
};

export default Search;
