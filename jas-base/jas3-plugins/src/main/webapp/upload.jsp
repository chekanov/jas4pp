<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@taglib prefix="jas" uri="/WEB-INF/tlds/jas"%>

<html>
    <head>
        <title>Upload plugin file</title>
    </head>
    <body>
        <h1>Upload plugin file</h1>
        <c:if test="${!empty param.xml}">
            <jas:loadPlugin xml="${param.xml}" dataSource="jdbc/config-srs"/>
        </c:if>
        <form action="upload.jsp" method="post" enctype="multipart/form-data">
            <input type="file" name="xml"/>
            <input type="submit" value="Upload"/>
        </form>
    </body>
</html>