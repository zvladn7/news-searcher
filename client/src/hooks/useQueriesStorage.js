import { useCallback, useState } from 'react';
import { getFromLocalStorage, setToLocalStorage } from '../utils/localStorage';

const LOCAL_STORAGE_KEY = 'news-searcher-queries-history';
const MAX_COUNT_QUERIES_IN_HISTORY = 100;

export const useQueriesStorage = () => {
  const [queriesHistory, setQueriesHistory] = useState(getFromLocalStorage(LOCAL_STORAGE_KEY) || []);

  const addQueryIntoHistory = useCallback((query) => {
    const newQueries = [...queriesHistory];

    if (newQueries.length >= MAX_COUNT_QUERIES_IN_HISTORY) {
      newQueries.pop();
    }

    const index = newQueries.findIndex(
      (queryStorage) => queryStorage.toLowerCase().trim() === query.toLowerCase().trim()
    );
    if (index > -1) {
      newQueries.splice(index, 1);
    }

    newQueries.unshift(query);
    setQueriesHistory(newQueries);
    setToLocalStorage(LOCAL_STORAGE_KEY, newQueries);
  }, [queriesHistory]);

  const deleteQueryFromHistory = useCallback((query) => {
    const newQueries = [...queriesHistory];
    const index = newQueries.findIndex(
      (queryStorage) => queryStorage.toLowerCase().trim() === query.toLowerCase().trim()
    );

    if (index > -1) {
      newQueries.splice(index, 1);
      setQueriesHistory(newQueries);
      setToLocalStorage(LOCAL_STORAGE_KEY, newQueries);
    }
  }, [queriesHistory]);

  return [queriesHistory, addQueryIntoHistory, deleteQueryFromHistory];
};
