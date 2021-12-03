import React from 'react';
import Loader from '../../components/ui/loader';

const NotFoundPage = () => {

  return (
    <div className="not-found-page">
      <Loader text="Error 404" />
    </div>
  );
}

export default NotFoundPage;
