import { DESKTOP_BREAKPOINT, DESKTOP_VIEW, MOBILE_VIEW, TABLET_BREAKPOINT, TABLET_VIEW } from '../constants/view';

export const getWindowProps = () => ({
  width: window.innerWidth,
  height: window.innerHeight,
  viewType:
    window.innerWidth >= DESKTOP_BREAKPOINT
      ? DESKTOP_VIEW
      : window.innerWidth >= TABLET_BREAKPOINT
      ? TABLET_VIEW
      : MOBILE_VIEW,
});
