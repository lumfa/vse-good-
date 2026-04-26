using FeelJournal.Data;
using FeelJournal.DTOs;
using FeelJournal.Models;
using FeelJournal.Security;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Services;

public class AuthService
{
    private readonly AppDbContext _db;
    private readonly JwtService _jwt;

    public AuthService(AppDbContext db, JwtService jwt)
    {
        _db = db;
        _jwt = jwt;
    }

    public async Task<AuthResponse?> RegisterAsync(RegisterRequest req)
    {
        if (await _db.Users.AnyAsync(u => u.Email == req.Email))
            return null;

        var user = new User
        {
            Email = req.Email,
            Password = PasswordHasher.Hash(req.Password),
            Name = AesEncryptor.Encrypt(req.Name)
        };

        _db.Users.Add(user);
        await _db.SaveChangesAsync();

        return BuildAuthResponse(user);
    }

    public async Task<AuthResponse?> LoginAsync(LoginRequest req)
    {
        var user = await _db.Users.FirstOrDefaultAsync(u => u.Email == req.Email);
        if (user == null || !PasswordHasher.Verify(req.Password, user.Password))
            return null;

        return BuildAuthResponse(user);
    }

    public async Task<bool> InitiateRecoveryAsync(string email)
    {
        var user = await _db.Users.FirstOrDefaultAsync(u => u.Email == email);
        if (user == null) return false;

        var token = Convert.ToBase64String(Guid.NewGuid().ToByteArray()).TrimEnd('=');
        return true;
    }

    public async Task<bool> ResetPasswordAsync(ResetPasswordRequest req)
    {
        var user = await _db.Users.FirstOrDefaultAsync(u => u.Email == req.Email);
        if (user == null) return false;

        user.Password = PasswordHasher.Hash(req.NewPassword);
        user.UpdatedAt = DateTime.UtcNow;
        await _db.SaveChangesAsync();
        return true;
    }

    private AuthResponse BuildAuthResponse(User user)
    {
        var accessToken = _jwt.GenerateAccessToken(user.Id, user.Email, user.Name);
        var refreshToken = _jwt.GenerateRefreshToken();

        return new AuthResponse
        {
            Token = accessToken,
            RefreshToken = refreshToken,
            User = new UserDto
            {
                Id = user.Id,
                Email = user.Email,
                Name = AesEncryptor.Decrypt(user.Name)
            }
        };
    }
}
