<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>FireOwl</title>
<script type="text/javascript">
 
function getXMLObject()  //XML OBJECT
{
   var xmlHttp = false;
   try {
     xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")  // For Old Microsoft Browsers
   }
   catch (e) {
     try {
       xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")  // For Microsoft IE 6.0+
     }
     catch (e2) {
       xmlHttp = false   // No Browser accepts the XMLHTTP Object then false
     }
   }
   if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
     xmlHttp = new XMLHttpRequest();        //For Mozilla, Opera Browsers
   }
   return xmlHttp;  // Mandatory Statement returning the ajax object created
}
 
var xmlhttp = new getXMLObject();	//xmlhttp holds the ajax object
 
function ajaxFunction() {
  if(xmlhttp) {
	 <%
	 	String url = request.getParameter("url").replace("?", "%3F");
	 /*
	 	String url_enc = request.getParameter("url").replace("?", "%3F");
	 	String url = url_enc;
	 	String url_completa = url_enc;
	 	if (url_completa.indexOf("#") > 0) {
			String lixo = url_completa.substring(url_completa.indexOf("#"));
			url = url_completa.replace(lixo, "");
			url = url + "\"";
	 	}
	 	*/
	%>
    xmlhttp.open("GET","Consulta?url=" + <%=url%>,true); //gettime will be the servlet name
    xmlhttp.onreadystatechange  = handleServerResponse;
    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xmlhttp.send(null);
  }
}
 
function handleServerResponse() {
   if (xmlhttp.readyState == 4) {
     if(xmlhttp.status == 200) {
    	 document.getElementById("main").innerHTML=xmlhttp.responseText; //Update the HTML Form element 
     }
     else {
        alert("Error during AJAX call. Please try again");
     }
   }
}
</script>

<style>
div {
	font-family: verdana;
	font-size: 12px;
}

div#label {
	background-color: #FFA500;
	font-size: 13px;
	padding: 5px;
	margin: 5px;
	font-weight: bold;
}

div#abst {
	border-style: solid;
	border-width: 1px;
	padding: 5px;
	border-color: #FFA500;
	margin: 5px;
}
</style>

</head>
<body id="home" onload="javascript:ajaxFunction();">
	<div id="main" style="width:100%;text-align:center;padding-top:20px;font-family:verdana;font-size:13px">
		<p>Analisando conteúdo da página<br/><%=url.replace("%3F", "?")%></p>
		<img src="carregando.gif" />
	</div>
</body>
</html>