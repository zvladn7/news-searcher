import { useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';

export const useSearchQueryResultText = (page = 0, initQuery = '') => {
  const {response, isLoading: resultTextLoading, error: resultTextError, sendRequest} = useApi();
  const [resultText, setResultText] = useState([]);
  const [resultTextCount, setResultTextCount] = useState(0);

  useEffect(() => {
    sendRequest({
      url: `/search/${page + 1}`,
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify({
        query: initQuery,
      }),
    });
  }, []);

  useEffect(() => {
    setResultText(response?.searchItems || []);
    setResultTextCount(response?.totalCount || 0);
  }, [response]);

  const resultTextSendRequest = (page, query) => {
    sendRequest({
      url: `/search/${page + 1}`,
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify({
        query: query,
      }),
    });
  };

  return [resultText, resultTextCount, resultTextLoading, resultTextError, resultTextSendRequest];
};
