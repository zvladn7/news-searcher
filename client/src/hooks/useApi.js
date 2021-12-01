import { useCallback, useState } from 'react';
import axios from 'axios';

const BASE_URL = '';

export const useApi = ({url, method, body = null, headers = null}) => {
  const [response, setResponse] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const sendRequest = useCallback(async () => {
    setIsLoading(true);
    setError('');

    await axios[method](`${BASE_URL}${url}`, JSON.parse(headers), JSON.parse(body))
      .then((response) => {
        setResponse(response);
      })
      .catch((error) => {
        setError(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [url, method, body, headers]);

  return {response, isLoading, error, sendRequest};
};
