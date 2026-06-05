import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api';

const CreatePost = () => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [categories, setCategories] = useState([]);
  const [selectedCategoryIds, setSelectedCategoryIds] = useState([]);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) navigate('/login');
    const fetchCategories = async () => {
      try {
        const res = await api.get('/categories');
        setCategories(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchCategories();
  }, [user, navigate]);

  const toggleCategory = (id) => {
    if (selectedCategoryIds.includes(id)) {
      setSelectedCategoryIds(selectedCategoryIds.filter(catId => catId !== id));
    } else {
      setSelectedCategoryIds([...selectedCategoryIds, id]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/posts', { 
        title, 
        content, 
        categoryIds: selectedCategoryIds 
      });
      navigate('/');
    } catch (err) {
      alert('Failed to create post');
    }
  };

  return (
    <div className="create-post-form">
      <h2>Create New Post</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Title</label>
          <input 
            type="text" 
            value={title} 
            onChange={(e) => setTitle(e.target.value)} 
            required 
          />
        </div>
        <div className="form-group">
          <label>Content</label>
          <textarea 
            rows="10" 
            value={content} 
            onChange={(e) => setContent(e.target.value)} 
            required
          ></textarea>
        </div>
        <div className="form-group">
          <label>Categories</label>
          <div className="category-selection">
            {categories.map(cat => (
              <label key={cat.id} className="category-checkbox">
                <input 
                  type="checkbox" 
                  checked={selectedCategoryIds.includes(cat.id)}
                  onChange={() => toggleCategory(cat.id)}
                />
                {cat.name}
              </label>
            ))}
          </div>
        </div>
        <button type="submit" className="btn">Create Post</button>
      </form>
    </div>
  );
};

export default CreatePost;
