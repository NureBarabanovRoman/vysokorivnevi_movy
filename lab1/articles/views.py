from django.shortcuts import render, get_object_or_404
from django.contrib.auth.models import User
from rest_framework import viewsets
from .models import Article
from .serializers import ArticleSerializer

def article_list(request):
    articles = Article.objects.all()
    return render(request, 'articles/article_list.html', {'articles': articles})

def article_detail(request, pk):
    article = get_object_or_404(Article, pk=pk)
    return render(request, 'articles/article_detail.html', {'article': article})

def articles_by_author(request, author_id):
    author = get_object_or_404(User, pk=author_id)
    articles = Article.objects.filter(author=author)
    return render(request, 'articles/article_list.html', {'articles': articles, 'author': author})

class ArticleViewSet(viewsets.ModelViewSet):
    queryset = Article.objects.all()
    serializer_class = ArticleSerializer
