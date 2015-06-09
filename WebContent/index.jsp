<!DOCTYPE html>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html>
<head>
<title>Kaffee-Finder</title>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/bootstrap-ics.css">
<link rel="stylesheet" href="css/style.css">

<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
		<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
		<script src="http://cdnjs.cloudflare.com/ajax/libs/es5-shim/2.0.8/es5-shim.min.js"></script>
	<![endif]-->

<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</head>

<body>
	<div class="container">
		<div class="row header">
			<div class="col-md-12">
				<h1>Kaffee-Finder</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<jsp:include page="menu.html" />
			</div>
			<div class="col-md-12">
				<p>Willkommen beim Kaffee-Finder!<br><br>
				Wählen Sie aus dem obigen Menü aus - Sie können Kaffees suchen, Zutaten auswählen und neue Kaffees erstellen.<br>
				<img src="http://www.geniessbar-mobil.de/medien/Bilder/gb_kaffee_large.jpg" alt="Kaffee-Finder">
			</div>
		</div>
	</div>
</body>
</html>