import React, { useCallback, useState } from 'react';
import InputSearch from '../../components/inputSearch';
import SearchButton from '../../components/ui/searchButton';
import { useQueryParams } from '../../hooks/useQueryParams';
import {
  PAGE_QUERY_PARAMETER,
  SEARCH_PAGE_PATH,
  SEARCH_VALUE_QUERY_PARAMETER,
  TAB_QUERY_PARAMETER,
} from '../../constants/url';
import { useLanguage } from '../../hooks/useLanguage';
import { searchButtonText, searchInputPlaceholder } from '../../constants/language';
import { useQueriesStorage } from '../../hooks/useQueriesStorage';

const Welcome = () => {
  const [queriesHistory, addQueryIntoHistory, deleteQueryFromHistory] = useQueriesStorage();
  const [queryParams, navigateWithQueryParams] = useQueryParams();
  const [inputSearchValue, setInputSearchValue] = useState(queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || '');
  const [language] = useLanguage();

  const handleOnChangeInputSearchValue = (value) => setInputSearchValue(value);

  const handleOnSearch = useCallback((query) => {
    addQueryIntoHistory(query);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: query,
      [TAB_QUERY_PARAMETER]: 0,
      [PAGE_QUERY_PARAMETER]: 0,
    });
  }, [addQueryIntoHistory]);

  return (
    <div className="welcome">
      <h1 className="welcome__title">Search</h1>
      <div className="welcome__search-input">
        <InputSearch
          value={inputSearchValue}
          onChange={handleOnChangeInputSearchValue}
          placeholder={searchInputPlaceholder[language]}
          history={queriesHistory}
          onDeleteHistoryItem={deleteQueryFromHistory}
          onEnterDown={handleOnSearch}
        />
      </div>
      {!!inputSearchValue &&
        <SearchButton text={searchButtonText[language]} onClick={() => handleOnSearch(inputSearchValue)} />}
    </div>
  );
};

export default Welcome;
