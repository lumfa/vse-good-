using System.ComponentModel.DataAnnotations;

namespace FeelJournal.Models;

public class Category
{
    [Key]
    public int Id { get; set; }

    [Required]
    [StringLength(255)]
    public string Name { get; set; } = string.Empty;

    [Required]
    public CategoryType Type { get; set; } = CategoryType.Neutral;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<EmotionEntry> EmotionEntries { get; set; } = new List<EmotionEntry>();
}
