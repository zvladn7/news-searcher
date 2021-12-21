import { createSearchParams, useLocation, useNavigate } from 'react-router-dom';
import { useCallback, useMemo } from 'react';

export const useQueryParams = () => {
  const {search} = useLocation();
  const navigate = useNavigate();

  const navigateWithQueryParams = useCallback((pathname = '/', payload = {}, replace = false) => {
    navigate({
      pathname: pathname,
      search: `?${createSearchParams(payload)}`,
      replace: replace,
    });
  }, [navigate]);

  return [useMemo(() => new URLSearchParams(search), [search]), navigateWithQueryParams];
};
