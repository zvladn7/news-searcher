import { useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';

//TODO: удалить это, как исправят бэк
const getSimilarFromResponse = (response) => response.map((similar) => similar.title);

export const useSimilarQueryResult = (initQuery = '') => {
  const {response, isLoading: similarQueryLoading, error: similarQueryError, sendRequest} = useApi();
  const [similarQuery, setSimilarQuery] = useState([]);

  useEffect(() => {
    sendRequest({
      url: '/search/similar',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify(initQuery),
    });
  }, []);

  useEffect(() => {
    if (response) {
      setSimilarQuery(getSimilarFromResponse(response))
    } else {
      setSimilarQuery([]);
    }
  }, [response]);

  const similarQuerySendRequest = (query) => {
    sendRequest({
      url: '/search/similar',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify(query),
    });
  };

  return [similarQuery, similarQueryLoading, similarQueryError, similarQuerySendRequest];
};

