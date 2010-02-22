<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MythPodcaster :: Programs</title>
<link rel="stylesheet" href="styles/html.css" type="text/css">
</head>
<body>
<h1>Transcoded Series Subscriptions</h1>

<c:if test="${not empty mythpodcaster_series_subscriptions}">
    <table border="1">
      <tr><td><b>Title</b></td><td><b>Profile</b></td><td><b>Actions</b></td><td><b>Feed</b></td></tr>
      <c:forEach items="${mythpodcaster_series_subscriptions}" var="series">
       	<tr><td><c:out value="${series.title}"/></td><td><c:out value="${series.transcodeProfile}" /></td><td align="right"><a href="subscriptions.htm?action=unsubscribe&seriesId=${series.seriesId}"/>Unsubscribe</a></td><td align="right"><a href="${applicationURL}/${series.seriesId}${feedFileExtension}"/>Feed</a></td></tr>
      </c:forEach>
    </table>
   </c:if>

<h1>All Remaining Series</h1>

<c:if test="${not empty mythpodcaster_series}">
    <table border="1">
      <tr><td><b>Title</b></td><td><b>Earliest Recording</b></td><td><b>Most Recent Recording</b></td><td><b>Actions</b></td></tr>
      <c:forEach items="${mythpodcaster_series}" var="series">
        <tr><td><c:out value="${series.title}"/></td><td align="right"><c:out value="${series.earliestRecordingTimestamp}"/></td><td align="right"><c:out value="${series.lastRecordingTimestamp}"/></td><td align="right"><a href="subscriptions.htm?action=subscribe&seriesId=${series.seriesId}"/>Subscribe</a></td></tr>
      </c:forEach>
    </table>
   </c:if>

</body>
</html>
