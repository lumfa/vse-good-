using System.ComponentModel.DataAnnotations;

namespace FeelJournal.Models;

public class Emotion
{
    [Key]
    public int Id { get; set; }

    [Required]
    [StringLength(255)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [StringLength(255)]
    public string Icon { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<EmotionEntry> EmotionEntries { get; set; } = new List<EmotionEntry>();
}
