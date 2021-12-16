import { useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';

export const useSimilarQueryResult = (initQuery = '') => {
  const {response, isLoading: similarQueryLoading, error: similarQueryError, sendRequest} = useApi();
  const [similarQuery, setSimilarQuery] = useState([]);

  useEffect(() => {
    sendRequest({
      url: '/search/similar',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify({
        query: initQuery,
      }),
    });
  }, []);

  useEffect(() => {
    if (response) {
      setSimilarQuery(response)
    } else {
      setSimilarQuery([]);
    }
  }, [response]);

  const similarQuerySendRequest = (query) => {
    sendRequest({
      url: '/search/similar',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify({
        query: query,
      }),
    });
  };

  return [similarQuery, similarQueryLoading, similarQueryError, similarQuerySendRequest];
};

