using System.Security.Claims;
using FeelJournal.DTOs;
using FeelJournal.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly AuthService _auth;

    public AuthController(AuthService auth) => _auth = auth;

    [HttpPost("register")]
    public async Task<ActionResult<AuthResponse>> Register([FromBody] RegisterRequest req)
    {
        var result = await _auth.RegisterAsync(req);
        if (result == null) return Conflict(new { message = "Email already exists" });
        return Ok(result);
    }

    [HttpPost("login")]
    public async Task<ActionResult<AuthResponse>> Login([FromBody] LoginRequest req)
    {
        var result = await _auth.LoginAsync(req);
        if (result == null) return Unauthorized(new { message = "Invalid email or password" });
        return Ok(result);
    }

    [HttpPost("recovery")]
    public async Task<ActionResult> Recovery([FromBody] RecoveryRequest req)
    {
        var ok = await _auth.InitiateRecoveryAsync(req.Email);
        if (!ok) return NotFound(new { message = "Email not found" });
        return Ok(new { message = "Recovery email sent" });
    }

    [HttpPost("reset-password")]
    public async Task<ActionResult> ResetPassword([FromBody] ResetPasswordRequest req)
    {
        var ok = await _auth.ResetPasswordAsync(req);
        if (!ok) return BadRequest(new { message = "Invalid reset request" });
        return Ok(new { message = "Password updated" });
    }

    [Authorize]
    [HttpGet("me")]
    public ActionResult<UserDto> Me()
    {
        var userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var email = User.FindFirstValue(ClaimTypes.Email)!;
        var name = User.FindFirstValue(ClaimTypes.Name)!;

        return Ok(new UserDto { Id = userId, Email = email, Name = name });
    }
}
