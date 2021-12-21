import { useEffect, useState } from 'react';
import { getWindowProps } from '../utils/window';

export const useWindowSize = () => {
  const [windowSize, setWindowSize] = useState({
    width: undefined,
    height: undefined,
    viewType: undefined,
  });

  const handleResize = () => {
    setWindowSize(getWindowProps());
  };

  useEffect(() => {
    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return windowSize;
};
