from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

router = DefaultRouter()
router.register(r'articles-api', views.ArticleViewSet)

urlpatterns = [
    path('', views.article_list, name='article_list'),
    path('article/<int:pk>/', views.article_detail, name='article_detail'),
    path('author/<int:author_id>/', views.articles_by_author, name='articles_by_author'),
    path('api/', include(router.urls)),
]
