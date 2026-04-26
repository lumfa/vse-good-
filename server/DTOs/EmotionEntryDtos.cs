using System.ComponentModel.DataAnnotations;

namespace FeelJournal.DTOs;

public class EmotionEntryDto
{
    public int Id { get; set; }
    public string Date { get; set; } = string.Empty;
    public string? Note { get; set; }
    public int UserId { get; set; }
    public int CategoryId { get; set; }
    public int EmotionId { get; set; }
    public CategoryDto? Category { get; set; }
    public EmotionDto? Emotion { get; set; }
    public string CreatedAt { get; set; } = string.Empty;
    public string UpdatedAt { get; set; } = string.Empty;
}

public class CreateEmotionEntryRequest
{
    [Required]
    public string Date { get; set; } = string.Empty;

    public string? Note { get; set; }

    [Required]
    public int CategoryId { get; set; }

    [Required]
    public int EmotionId { get; set; }
}

public class UpdateEmotionEntryRequest
{
    [Required]
    public string Date { get; set; } = string.Empty;

    public string? Note { get; set; }

    [Required]
    public int CategoryId { get; set; }

    [Required]
    public int EmotionId { get; set; }
}

public class PagedResponse<T>
{
    public List<T> Items { get; set; } = new();
    public int Page { get; set; }
    public int PageSize { get; set; }
    public int TotalCount { get; set; }
    public int TotalPages => (int)Math.Ceiling(TotalCount / (double)PageSize);
    public bool HasNext => Page < TotalPages;
    public bool HasPrev => Page > 1;
}
