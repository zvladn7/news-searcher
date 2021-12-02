import React, { useCallback, useState } from 'react';
import { useQueryParams } from '../../hooks/useQueryParams';
import Header from '../../components/header';
import { useLanguage } from '../../hooks/useLanguage';
import { useQueriesStorage } from '../../hooks/useQueriesStorage';
import { SEARCH_PAGE_PATH, SEARCH_VALUE_QUERY_PARAMETER, TAB_QUERY_PARAMETER } from '../../constants/url';

const Search = () => {
  const [queryParams, navigateWithQueryParams] = useQueryParams();
  const [inputSearchValue, setInputSearchValue] = useState(queryParams.get(SEARCH_VALUE_QUERY_PARAMETER) || '');
  const [queriesHistory, addQueryIntoHistory, deleteQueryFromHistory] = useQueriesStorage();
  const [language, changeLanguage] = useLanguage();
  const [tab, setTab] = useState(Number(queryParams.get(TAB_QUERY_PARAMETER)) || 0);

  const handleOnChangeInputSearchValue = (value) => setInputSearchValue(value);

  const handleOnSearch = useCallback((query) => {
    addQueryIntoHistory(query);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: query,
      [TAB_QUERY_PARAMETER]: tab,
    });
  }, [addQueryIntoHistory, tab]);

  const handleOnChangeTab = useCallback((tabId) => {
    setTab(tabId);
    navigateWithQueryParams(SEARCH_PAGE_PATH, {
      [SEARCH_VALUE_QUERY_PARAMETER]: inputSearchValue,
      [TAB_QUERY_PARAMETER]: tabId,
    });
  }, [tab, inputSearchValue]);

  return (
    <div className="search">
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
      <div className="search__content" />
    </div>
  );
};

export default Search;
