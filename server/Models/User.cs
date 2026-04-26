using System.ComponentModel.DataAnnotations;

namespace FeelJournal.Models;

public class User
{
    [Key]
    public int Id { get; set; }

    [Required]
    [EmailAddress]
    [StringLength(255)]
    public string Email { get; set; } = string.Empty;

    [Required]
    [StringLength(255)]
    public string Password { get; set; } = string.Empty;

    [Required]
    [StringLength(255)]
    public string Name { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<EmotionEntry> EmotionEntries { get; set; } = new List<EmotionEntry>();
    public ICollection<Notification> Notifications { get; set; } = new List<Notification>();
}
