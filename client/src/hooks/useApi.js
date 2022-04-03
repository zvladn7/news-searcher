import { useCallback, useState } from 'react';
import axios from 'axios';

const BASE_URL = 'http://localhost:9090';

export const useApi = () => {
  const [response, setResponse] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const sendRequest = useCallback(async ({url, method, body = {}, headers = {}}) => {
    setIsLoading(true);
    setError('');

    await axios[method](`${BASE_URL}${url}`, JSON.parse(body), JSON.parse(headers))
      .then((response) => {
        setResponse(response.data);
      })
      .catch((error) => {
        setError(error);
        setResponse(null);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  return {response, isLoading, error, sendRequest};
};
