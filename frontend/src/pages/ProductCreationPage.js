import React, { useState } from 'react';
import Config from '../Config';

const ProductCreationPage = () => {
  const [productData, setProductData] = useState({
    name: '',
    description: '',
    basePrice: '',
    bidDuration: '',
    bidIncrements: ''
  });
  const [images, setImages] = useState([]);

  let baseURL = Config.baseURL;

  const handleInputChange = (e) => {
    setProductData({ ...productData, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    setImages(e.target.files);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('product', JSON.stringify(productData));
    for (let i = 0; i < images.length; i++) {
      formData.append('images', images[i]);
    }

    try {
      const response = await fetch(`${baseURL}/upload/{bitsId}`, {
        method: 'POST',
        body: formData
      });

      if (response.ok) {
        alert('Product uploaded successfully!');
      } else {
        alert('Failed to upload product');
      }
    } catch (error) {
      console.error('There was an error uploading the product!', error);
    }
  };

  return (
    <div>
      <h1>Create New Product</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Name:</label>
          <input type="text" name="name" value={productData.name} onChange={handleInputChange} required />
        </div>
        <div>
          <label>Description:</label>
          <input type="text" name="description" value={productData.description} onChange={handleInputChange} required />
        </div>
        <div>
          <label>Base Price:</label>
          <input type="number" name="basePrice" value={productData.basePrice} onChange={handleInputChange} required />
        </div>
        <div>
          <label>Bid Duration (hours):</label>
          <input type="number" name="bidDuration" value={productData.bidDuration} onChange={handleInputChange} required />
        </div>
        <div>
          <label>Bid Increments:</label>
          <input type="number" name="bidIncrements" value={productData.bidIncrements} onChange={handleInputChange} required />
        </div>
        <div>
          <label>Upload Images:</label>
          <input type="file" name="images" multiple onChange={handleFileChange} required />
        </div>
        <button type="submit">Upload Product</button>
      </form>
    </div>
  );
};

export default ProductCreationPage;
