import React, { useCallback, useState } from 'react';
import InputSearch from '../../components/inputSearch';
import { inputSearchHistory } from '../../mocks/inputSearch';
import SearchButton from '../../components/ui/searchButton';
import LanguageButton from '../../components/ui/languageButton';
import BlockResultSearch from '../../components/ui/blockResultSearch';
import { blockResultSearch } from '../../mocks/blockResultSearch';
import SimilarQueries from '../../components/ui/similarQueries';
import { similarQueries } from '../../mocks/similarQueries';
import { useWindowSize } from '../../hooks/useWindowSize';
import TermsBlock from '../../components/ui/termsBlock';
import { termsSuggestions } from '../../mocks/termsBlock';
import ImageBlock from '../../components/ui/imageBlock';
import { imageResult } from '../../mocks/imageBlock';
import { ENGLISH_LANGUAGE, RUSSIAN_LANGUAGE } from '../../constants/language';

const UiKit = () => {
  // TODO: вынести в класс с историей поиска
  const [inputSearchHistoryQueries, setInputSearchHistoryQueries] = useState(inputSearchHistory);
  const [inputSearchValue, setInputSearchValue] = useState('');

  const handleOnEnterDown = useCallback((query) => {
    const newQueries = [...inputSearchHistoryQueries];
    const index = newQueries.indexOf(query);
    if (index > -1) {
      newQueries.splice(index, 1);
    }

    newQueries.unshift(query);
    setInputSearchHistoryQueries(newQueries);
  }, [inputSearchHistoryQueries]);

  const handleOnDeleteHistoryItem = useCallback((query) => {
    const newQueries = [...inputSearchHistoryQueries];
    const index = newQueries.indexOf(query);
    if (index > -1) {
      newQueries.splice(index, 1);
      setInputSearchHistoryQueries(newQueries);
    }
  }, [inputSearchHistoryQueries]);

  const [languageButtonType, setLanguageButtonType] = useState(ENGLISH_LANGUAGE);

  const handleOnClickLanguageButton = useCallback(() => {
    languageButtonType === ENGLISH_LANGUAGE
      ? setLanguageButtonType(RUSSIAN_LANGUAGE)
      : setLanguageButtonType(ENGLISH_LANGUAGE);
  }, [languageButtonType]);

  const windowSize = useWindowSize();
  const {viewType} = windowSize;

  const handleOnClickItemSimilarQueries = (query) => {
    setInputSearchValue(query);
  };

  return (
    <div className="ui-kit">
      <div className="ui-kit__component">
        <InputSearch
          value={inputSearchValue}
          onChange={(value) => setInputSearchValue(value)}
          onEnterDown={handleOnEnterDown}
          onDeleteHistoryItem={handleOnDeleteHistoryItem}
          history={inputSearchHistoryQueries}
        />
      </div>
      <div className="ui-kit__component">
        <SearchButton text="Search" onClick={() => console.log('Search button click!')} />
      </div>
      <div className="ui-kit__component">
        <LanguageButton type={languageButtonType} onClick={handleOnClickLanguageButton} />
      </div>
      <div className="ui-kit__component">
        <BlockResultSearch
          title={blockResultSearch.searchItems[0].title}
          link={blockResultSearch.searchItems[0].link}
        />
      </div>
      <div className="ui-kit__component">
        <SimilarQueries
          title="Related searches"
          queries={similarQueries}
          onClickItem={handleOnClickItemSimilarQueries}
          viewType={viewType}
        />
      </div>
      <div className="ui-kit__component">
        <TermsBlock title={termsSuggestions.title} terms={termsSuggestions.terms} />
      </div>
      <div className="ui-kit__component">
        <ImageBlock title={imageResult.title} imageUrl={imageResult.imageUrl} link={imageResult.link} />
      </div>
    </div>
  );
};

export default UiKit;
