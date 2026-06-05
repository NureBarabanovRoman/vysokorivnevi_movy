const express = require('express');
const router = express.Router();
const db = require('../db');
const auth = require('../middleware/auth');
const cache = require('../middleware/cache');

// GET all posts with filtering and search
router.get('/', cache, (req, res) => {
  const { search, category } = req.query;
  let query = `
    SELECT posts.*, users.username as author_name, GROUP_CONCAT(categories.name) as category_names
    FROM posts
    JOIN users ON posts.author_id = users.id
    LEFT JOIN post_categories ON posts.id = post_categories.post_id
    LEFT JOIN categories ON post_categories.category_id = categories.id
  `;
  const params = [];
  const whereClauses = [];

  if (search) {
    whereClauses.push('(posts.title LIKE ? OR posts.content LIKE ?)');
    params.push(`%${search}%`, `%${search}%`);
  }

  if (category) {
    whereClauses.push('post_categories.category_id = ?');
    params.push(category);
  }

  if (whereClauses.length > 0) {
    query += ' WHERE ' + whereClauses.join(' AND ');
  }

  query += ' GROUP BY posts.id ORDER BY posts.created_at DESC';

  try {
    const posts = db.prepare(query).all(...params);
    res.json(posts);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

// GET single post with comments
router.get('/:id', (req, res) => {
  try {
    const post = db.prepare(`
      SELECT posts.*, users.username as author_name
      FROM posts
      JOIN users ON posts.author_id = users.id
      WHERE posts.id = ?
    `).get(req.params.id);

    if (!post) {
      return res.status(404).json({ message: 'Post not found' });
    }

    const comments = db.prepare(`
      SELECT comments.*, users.username as author_name
      FROM comments
      JOIN users ON comments.author_id = users.id
      WHERE comments.post_id = ?
      ORDER BY comments.created_at DESC
    `).all(req.params.id);

    const categories = db.prepare(`
      SELECT categories.*
      FROM categories
      JOIN post_categories ON categories.id = post_categories.category_id
      WHERE post_categories.post_id = ?
    `).all(req.params.id);

    res.json({ ...post, comments, categories });
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

// POST a new post
router.post('/', auth, (req, res) => {
  const { title, content, categoryIds } = req.body;
  
  if (!title || !content) {
    return res.status(400).json({ message: 'Title and content are required' });
  }

  try {
    const insertPost = db.transaction(() => {
      const info = db.prepare('INSERT INTO posts (title, content, author_id) VALUES (?, ?, ?)')
        .run(title, content, req.user.id);
      
      const postId = info.lastInsertRowid;
      
      if (categoryIds && Array.isArray(categoryIds)) {
        const insertCategory = db.prepare('INSERT INTO post_categories (post_id, category_id) VALUES (?, ?)');
        for (const categoryId of categoryIds) {
          insertCategory.run(postId, categoryId);
        }
      }
      return postId;
    });

    const postId = insertPost();
    
    // Clear cache
    cache.cache.flushAll();
    
    res.status(201).json({ id: postId, title, content });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

// POST a comment
router.post('/:id/comments', auth, (req, res) => {
  const { content } = req.body;
  if (!content) {
    return res.status(400).json({ message: 'Comment content is required' });
  }

  try {
    const info = db.prepare('INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)')
      .run(req.params.id, req.user.id, content);
    
    res.status(201).json({ id: info.lastInsertRowid, content, author_id: req.user.id });
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
