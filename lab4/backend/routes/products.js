const express = require('express');
const { Product, Category } = require('../models');
const auth = require('../middleware/auth');
const router = express.Router();

router.get('/', async (req, res) => {
  const products = await Product.findAll({ include: Category });
  res.json(products);
});

router.post('/', auth, async (req, res) => {
  const product = await Product.create(req.body);
  res.status(201).json(product);
});

router.put('/:id', auth, async (req, res) => {
  await Product.update(req.body, { where: { id: req.params.id } });
  res.json({ message: 'Updated' });
});

router.delete('/:id', auth, async (req, res) => {
  await Product.destroy({ where: { id: req.params.id } });
  res.json({ message: 'Deleted' });
});

module.exports = router;
