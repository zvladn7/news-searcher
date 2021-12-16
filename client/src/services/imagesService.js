import { useCallback, useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';

const initPage = 1;

export const useSearchQueryResultImage = (initQuery = '') => {
  const {response, isLoading: resultImageLoading, error: resultImageError, sendRequest} = useApi();
  const [resultImage, setResultImage] = useState([]);
  const [resultImageCount, setResultImageCount] = useState(0);
  const [resultImagePage, setResultImagePage] = useState(initPage);

  useEffect(() => {
    sendRequest({
      url: '/search/image',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify({
        page: 1,
        query: initQuery,
      }),
    });
  }, []);

  useEffect(() => {
    const newResultImage = [...resultImage];

    setResultImage([...newResultImage, ...(response?.imageItems || [])]);
    setResultImageCount(response?.totalCount || 0);
  }, [response]);

  const resultImageSendRequest = useCallback((query, isNewSearch = false) => {
    if (isNewSearch) {
      setResultImage([]);
      setResultImageCount(0);
    }
    const newPage = isNewSearch ? initPage : resultImagePage + 1;
    setResultImagePage(newPage);
    sendRequest({
      url: '/search/image',
      method: 'post',
      headers: JSON.stringify({}),
      body: JSON.stringify(
        {
          page: newPage,
          query: query,
        }
      ),
    });
  }, [resultImagePage]);

  return [resultImage, resultImageCount, resultImageLoading, resultImageError, resultImageSendRequest];
};
