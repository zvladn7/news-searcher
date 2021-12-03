import imageUrl from '../assets/images/backgroundImage.png';

export const resultsImage = [
  ...[...Array(14)].map((item, index) => ({
    id: index,
    images: imageUrl,
    title: 'A long header from a website or document',
    link: 'http://www.google.com',
  })),

];
