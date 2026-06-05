const express = require('express');
const { Category } = require('../models');
const auth = require('../middleware/auth');
const router = express.Router();

router.get('/', async (req, res) => {
  const categories = await Category.findAll();
  res.json(categories);
});

router.post('/', auth, async (req, res) => {
  const category = await Category.create(req.body);
  res.status(201).json(category);
});

router.put('/:id', auth, async (req, res) => {
  await Category.update(req.body, { where: { id: req.params.id } });
  res.json({ message: 'Updated' });
});

router.delete('/:id', auth, async (req, res) => {
  await Category.destroy({ where: { id: req.params.id } });
  res.json({ message: 'Deleted' });
});

module.exports = router;
