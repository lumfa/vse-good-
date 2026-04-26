using System.Security.Cryptography;
using System.Text;

namespace FeelJournal.Security;

public static class AesEncryptor
{
    private static readonly byte[] DefaultKey = SHA256.HashData(Encoding.UTF8.GetBytes("FeelJournal_AES256_Key_2024!"));
    private static readonly byte[] DefaultIv = MD5.HashData(Encoding.UTF8.GetBytes("FeelJournal_IV"));

    public static string Encrypt(string plainText, byte[]? key = null, byte[]? iv = null)
    {
        key ??= DefaultKey;
        iv ??= DefaultIv;

        using var aes = Aes.Create();
        aes.Key = key;
        aes.IV = iv;
        aes.Mode = CipherMode.CBC;
        aes.Padding = PaddingMode.PKCS7;

        using var encryptor = aes.CreateEncryptor();
        var plainBytes = Encoding.UTF8.GetBytes(plainText);
        var cipherBytes = encryptor.TransformFinalBlock(plainBytes, 0, plainBytes.Length);

        return Convert.ToBase64String(cipherBytes);
    }

    public static string Decrypt(string cipherText, byte[]? key = null, byte[]? iv = null)
    {
        key ??= DefaultKey;
        iv ??= DefaultIv;

        using var aes = Aes.Create();
        aes.Key = key;
        aes.IV = iv;
        aes.Mode = CipherMode.CBC;
        aes.Padding = PaddingMode.PKCS7;

        using var decryptor = aes.CreateDecryptor();
        var cipherBytes = Convert.FromBase64String(cipherText);
        var plainBytes = decryptor.TransformFinalBlock(cipherBytes, 0, cipherBytes.Length);

        return Encoding.UTF8.GetString(plainBytes);
    }
}
