import React from 'react';
import PropTypes from 'prop-types';
import defaultImage from '../../../assets/images/backgroundImage.png';

const ImageBlock = (props) => {
  const {imageUrl, title, link} = props;

  return (
    <a className="image-block" href={link} target="_blank">
      <img className="image-block__image" src={imageUrl || defaultImage} alt={`Image from ${link}`} />
      <div className="image-block__bottom-bar">
        <span className="image-block__title">{title}</span>
        <span className="image-block__link">{link}</span>
      </div>
    </a>
  );
};

ImageBlock.propTypes = {
  imageUrl: PropTypes.string,
  title: PropTypes.string,
  link: PropTypes.string,
};

ImageBlock.defaultProps = {
  imageUrl: defaultImage,
  title: '',
  link: '',
};

export default ImageBlock;
