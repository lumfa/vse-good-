using System.Security.Cryptography;
using System.Text;

namespace FeelJournal.Security;

public static class PasswordHasher
{
    private const int SaltSize = 16;
    private const int HashSize = 32;
    private const int Iterations = 100_000;

    public static string Hash(string password)
    {
        var salt = RandomNumberGenerator.GetBytes(SaltSize);
        var hash = Rfc2898DeriveBytes.Pbkdf2(
            Encoding.UTF8.GetBytes(password), salt, Iterations, HashAlgorithmName.SHA256, HashSize);

        var bytes = new byte[SaltSize + HashSize];
        Array.Copy(salt, 0, bytes, 0, SaltSize);
        Array.Copy(hash, 0, bytes, SaltSize, HashSize);

        return Convert.ToBase64String(bytes);
    }

    public static bool Verify(string password, string storedHash)
    {
        var bytes = Convert.FromBase64String(storedHash);
        var salt = bytes[..SaltSize];
        var storedHashBytes = bytes[SaltSize..];

        var hash = Rfc2898DeriveBytes.Pbkdf2(
            Encoding.UTF8.GetBytes(password), salt, Iterations, HashAlgorithmName.SHA256, HashSize);

        return CryptographicOperations.FixedTimeEquals(hash, storedHashBytes);
    }
}
