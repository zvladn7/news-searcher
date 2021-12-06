import { useCallback, useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';

export const useSearchQueryResultImage = (initQuery = '') => {
  const {response, isLoading: resultImageLoading, error: resultImageError, sendRequest} = useApi();
  const [resultImage, setResultImage] = useState([]);
  const [resultImageCount, setResultImageCount] = useState(0);
  const [resultImagePage, setResultImagePage] = useState(1);

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

  const resultImageSendRequest = useCallback((query) => {
    const newPage = resultImagePage + 1;
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
