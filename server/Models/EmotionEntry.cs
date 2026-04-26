using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FeelJournal.Models;

public class EmotionEntry
{
    [Key]
    public int Id { get; set; }

    [Required]
    public DateOnly Date { get; set; }

    public string? Note { get; set; }

    [Required]
    [ForeignKey(nameof(User))]
    public int UserId { get; set; }

    [Required]
    [ForeignKey(nameof(Category))]
    public int CategoryId { get; set; }

    [Required]
    [ForeignKey(nameof(Emotion))]
    public int EmotionId { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public User User { get; set; } = null!;
    public Category Category { get; set; } = null!;
    public Emotion Emotion { get; set; } = null!;
}
