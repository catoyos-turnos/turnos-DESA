<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="_raiz" scope="page" value="${pageContext.request.contextPath}"/>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="icon" href="${_raiz}/resources/img/favicon.ico">
		
		<link rel="stylesheet" href="${_raiz}/resources/css/bootstrap.min.css">
		<link rel="stylesheet" href="${_raiz}/resources/css/bootstrap-theme.min.css">
		<link rel="stylesheet" href="${_raiz}/resources/css/ie10-viewport-bug-workaround.css">
		<link rel="stylesheet" href="${_raiz}/resources/css/main.css">
		
		<script src="${_raiz}/resources/js/ie10-viewport-bug-workaround.js"></script>
		<script src="${_raiz}/resources/js/vendor/modernizr-2.8.3.min.js"></script>
		<meta name="description" content="${_descripcion}">
		<title>${_titulo}</title>
		
	</head>