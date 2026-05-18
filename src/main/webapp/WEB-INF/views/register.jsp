<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
     <!-- CSS for custom style -->
    <style>
        .profile-pic-preview {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
            display: block;
            margin: 0 auto 0.5rem auto;
            border: 3px solid #2c3e50;
        }
        .profile-pic-placeholder {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background-color: #e0e0e0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 0.5rem auto;
            font-size: 2rem;
            color: #999;
            border: 3px dashed #ccc;
        }
        .pic-upload-section {
            text-align: center;
            margin-bottom: 1.2rem;
        }
        .pic-upload-label {
            display: inline-block;
            margin-top: 0.5rem;
            font-size: 0.85rem;
            color: #555;
            cursor: pointer;
        }
        .file-input-hint {
            font-size: 0.78rem;
            color: #888;
            margin-top: 4px;
        }
    </style>
</head>
<body class="auth-page">

    <div class="auth-card">
        <h1>Create Account</h1>
        <p>Join ParkEase to manage your parking easily.</p>

        <!-- Shows error message if registration fails -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!--
            Registration form.
            enctype="multipart/form-data" is required for file upload.
            Handled by UserServlet with @MultipartConfig annotation.
        -->
        <form action="${pageContext.request.contextPath}/UserServlet" method="POST"
              enctype="multipart/form-data">
            <input type="hidden" name="action" value="doRegister">

            <!-- Section for profile picture upload -->
            <div class="pic-upload-section">
                <div class="profile-pic-placeholder" id="picPlaceholder">&#128100;</div>
                <img id="picPreview" class="profile-pic-preview"
                     src="" alt="Profile Preview"
                     style="display:none;">
                <label for="profilePic" class="pic-upload-label">
                    Upload Profile Picture (optional)
                </label>
                <input type="file" id="profilePic" name="profilePic"
                       accept="image/jpeg,image/png,image/gif"
                       style="display:none;"
                       onchange="previewImage(this)">
                <p class="file-input-hint">JPG, PNG or GIF — max 2 MB</p>
            </div>

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name"
                       class="form-input" required placeholder="Enter your full name">
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone"
                       class="form-input" required placeholder="Enter phone number">
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email"
                       class="form-input" required placeholder="Enter email address">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       class="form-input" required placeholder="Create a password">
            </div>

            <button type="submit" class="btn-primary">Register Now</button>
        </form>

        <!-- Link for login page for already register user -->
        <div class="auth-footer">
            <p>Already have an account?
                <a href="${pageContext.request.contextPath}/LoginServlet">Login here</a>
            </p>
        </div>
    </div>

    <script>
        /* Preview the selected picture before upload */
        function previewImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('picPreview').src = e.target.result;
                    document.getElementById('picPreview').style.display = 'block';
                    document.getElementById('picPlaceholder').style.display = 'none';
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        /* Click on the label when the placeholder is clicked */
        document.getElementById('picPlaceholder').addEventListener('click', function() {
            document.getElementById('profilePic').click();
        });
        document.getElementById('picPreview').addEventListener('click', function() {
            document.getElementById('profilePic').click();
        });
    </script>

</body>
</html>
