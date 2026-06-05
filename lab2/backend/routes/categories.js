const express = require('express');
const router = express.Router();
const db = require('../db');
const cacheMiddleware = require('../middleware/cache');

// Get all categories
router.get('/', cacheMiddleware, (req, res) => {
  try {
    const categories = db.prepare('SELECT * FROM categories').all();
    res.json(categories);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
