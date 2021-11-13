import React from 'react';
import PropTypes from 'prop-types';

const TermsBlock = (props) => {
  const {title, terms} = props;

  return (
    <div className="terms-block">
      <span className="terms-block__title">{title}</span>
      <div className="terms-block__content">
        {terms.map((query, order) => (
          <div className="terms-block__item" key={`terms-block__item-${order}`}>
            <span className="terms-block__item-text">{query}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

TermsBlock.propTypes = {
  title: PropTypes.string,
  terms: PropTypes.arrayOf(PropTypes.string),
};

TermsBlock.defaultProps = {
  title: '',
  terms: [],
};

export default TermsBlock;
