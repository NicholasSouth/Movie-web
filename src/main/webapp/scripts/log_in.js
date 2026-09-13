function validateLoginForm()
{
    const login = document.getElementById("login").value.trim();
    const password = document.getElementById("password").value;

    // Check login field
    if (login === "")
    {
        alert("Please enter your username, email or phone.");
        return false;
    }

    // Check password field
    if (password === "")
    {
        alert("Please enter your password.");
        return false;
    }
    return true;
}