<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin</title>
    </head>
    <body>
        <h3>${message}</h3>
        <h1>Add new question</h1>
        <form method="POST" action="/Assignment3/add-quiz" style="display: flex; flex-direction: column; max-width: 50%">
            <label for="question">The question asked:</label>
            <input name="question" placeholder="Question" />
            <label for="1">First answer:</label>
            <input name="1" placeholder="1" />
            <label for="2">Second answer:</label>
            <input name="2" placeholder="2" />
            <label for="3">Third answer:</label>
            <input name="3" placeholder="3" />
            <label for="4">Fourth answer:</label>
            <input name="4" placeholder="4" />
            <label for="correct">The correct answer:</label>
            <input name="correct" placeholder="Correct" type="number" min="1" max="4" />
            <br />
            <button type="submit">Submit question</button>
        </form>
    </body>
</html>
