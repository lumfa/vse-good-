using System.ComponentModel.DataAnnotations;

namespace FeelJournal.DTOs;

public class NotificationDto
{
    public int Id { get; set; }
    public string Message { get; set; } = string.Empty;
    public string Date { get; set; } = string.Empty;
    public int UserId { get; set; }
    public bool IsRead { get; set; }
    public string CreatedAt { get; set; } = string.Empty;
}

public class CreateNotificationRequest
{
    [Required, StringLength(500)]
    public string Message { get; set; } = string.Empty;

    [Required]
    public string Date { get; set; } = string.Empty;
}

public class UpdateNotificationRequest
{
    [StringLength(500)]
    public string? Message { get; set; }
    public string? Date { get; set; }
    public bool? IsRead { get; set; }
}
