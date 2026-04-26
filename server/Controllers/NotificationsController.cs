using System.Security.Claims;
using FeelJournal.Data;
using FeelJournal.DTOs;
using FeelJournal.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/notifications")]
[Authorize]
public class NotificationsController : ControllerBase
{
    private readonly AppDbContext _db;

    public NotificationsController(AppDbContext db) => _db = db;

    private int UserId => int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);

    [HttpGet]
    public async Task<ActionResult<List<NotificationDto>>> GetAll([FromQuery] bool? unreadOnly = null)
    {
        var query = _db.Notifications
            .Where(n => n.UserId == UserId)
            .AsQueryable();

        if (unreadOnly == true)
            query = query.Where(n => !n.IsRead);

        var notifications = await query
            .OrderByDescending(n => n.Date)
            .ThenByDescending(n => n.CreatedAt)
            .Select(n => new NotificationDto
            {
                Id = n.Id,
                Message = n.Message,
                Date = n.Date.ToString("yyyy-MM-dd"),
                UserId = n.UserId,
                IsRead = n.IsRead,
                CreatedAt = n.CreatedAt.ToString("o")
            })
            .ToListAsync();

        return Ok(notifications);
    }

    [HttpPost]
    public async Task<ActionResult<NotificationDto>> Create([FromBody] CreateNotificationRequest req)
    {
        if (!DateOnly.TryParse(req.Date, out var date))
            return BadRequest(new { message = "Invalid date format. Use yyyy-MM-dd" });

        var notification = new Notification
        {
            Message = req.Message,
            Date = date,
            UserId = UserId
        };

        _db.Notifications.Add(notification);
        await _db.SaveChangesAsync();

        return CreatedAtAction(nameof(GetAll), new
        {
            Id = notification.Id,
            Message = notification.Message,
            Date = notification.Date.ToString("yyyy-MM-dd"),
            UserId = notification.UserId,
            IsRead = notification.IsRead,
            CreatedAt = notification.CreatedAt.ToString("o")
        });
    }

    [HttpPut("{id}")]
    public async Task<ActionResult<NotificationDto>> Update(int id, [FromBody] UpdateNotificationRequest req)
    {
        var notification = await _db.Notifications
            .FirstOrDefaultAsync(n => n.Id == id && n.UserId == UserId);

        if (notification == null) return NotFound();

        if (req.Message != null) notification.Message = req.Message;
        if (req.Date != null && DateOnly.TryParse(req.Date, out var date)) notification.Date = date;
        if (req.IsRead.HasValue) notification.IsRead = req.IsRead.Value;

        await _db.SaveChangesAsync();

        return Ok(new NotificationDto
        {
            Id = notification.Id,
            Message = notification.Message,
            Date = notification.Date.ToString("yyyy-MM-dd"),
            UserId = notification.UserId,
            IsRead = notification.IsRead,
            CreatedAt = notification.CreatedAt.ToString("o")
        });
    }

    [HttpPatch("{id}/read")]
    public async Task<ActionResult> MarkAsRead(int id)
    {
        var notification = await _db.Notifications
            .FirstOrDefaultAsync(n => n.Id == id && n.UserId == UserId);

        if (notification == null) return NotFound();

        notification.IsRead = true;
        await _db.SaveChangesAsync();

        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> Delete(int id)
    {
        var notification = await _db.Notifications
            .FirstOrDefaultAsync(n => n.Id == id && n.UserId == UserId);

        if (notification == null) return NotFound();

        _db.Notifications.Remove(notification);
        await _db.SaveChangesAsync();

        return NoContent();
    }
}
