using System.Security.Claims;
using FeelJournal.Data;
using FeelJournal.DTOs;
using FeelJournal.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/entries")]
[Authorize]
public class EmotionEntriesController : ControllerBase
{
    private readonly AppDbContext _db;

    public EmotionEntriesController(AppDbContext db) => _db = db;

    private int UserId => int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);

    [HttpGet]
    public async Task<ActionResult<PagedResponse<EmotionEntryDto>>> GetAll(
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20,
        [FromQuery] string? dateFrom = null,
        [FromQuery] string? dateTo = null,
        [FromQuery] int? categoryId = null,
        [FromQuery] int? emotionId = null)
    {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 20;

        var query = _db.EmotionEntries
            .Include(e => e.Category)
            .Include(e => e.Emotion)
            .Where(e => e.UserId == UserId)
            .AsQueryable();

        if (dateFrom != null && DateOnly.TryParse(dateFrom, out var df))
            query = query.Where(e => e.Date >= df);

        if (dateTo != null && DateOnly.TryParse(dateTo, out var dt))
            query = query.Where(e => e.Date <= dt);

        if (categoryId.HasValue)
            query = query.Where(e => e.CategoryId == categoryId.Value);

        if (emotionId.HasValue)
            query = query.Where(e => e.EmotionId == emotionId.Value);

        var totalCount = await query.CountAsync();

        var items = await query
            .OrderByDescending(e => e.Date)
            .ThenByDescending(e => e.CreatedAt)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(e => new EmotionEntryDto
            {
                Id = e.Id,
                Date = e.Date.ToString("yyyy-MM-dd"),
                Note = e.Note,
                UserId = e.UserId,
                CategoryId = e.CategoryId,
                EmotionId = e.EmotionId,
                Category = new CategoryDto
                {
                    Id = e.Category.Id,
                    Name = e.Category.Name,
                    Type = e.Category.Type.ToString()
                },
                Emotion = new EmotionDto
                {
                    Id = e.Emotion.Id,
                    Name = e.Emotion.Name,
                    Icon = e.Emotion.Icon
                },
                CreatedAt = e.CreatedAt.ToString("o"),
                UpdatedAt = e.UpdatedAt.ToString("o")
            })
            .ToListAsync();

        return Ok(new PagedResponse<EmotionEntryDto>
        {
            Items = items,
            Page = page,
            PageSize = pageSize,
            TotalCount = totalCount
        });
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<EmotionEntryDto>> GetById(int id)
    {
        var entry = await _db.EmotionEntries
            .Include(e => e.Category)
            .Include(e => e.Emotion)
            .Where(e => e.Id == id && e.UserId == UserId)
            .Select(e => new EmotionEntryDto
            {
                Id = e.Id,
                Date = e.Date.ToString("yyyy-MM-dd"),
                Note = e.Note,
                UserId = e.UserId,
                CategoryId = e.CategoryId,
                EmotionId = e.EmotionId,
                Category = new CategoryDto
                {
                    Id = e.Category.Id,
                    Name = e.Category.Name,
                    Type = e.Category.Type.ToString()
                },
                Emotion = new EmotionDto
                {
                    Id = e.Emotion.Id,
                    Name = e.Emotion.Name,
                    Icon = e.Emotion.Icon
                },
                CreatedAt = e.CreatedAt.ToString("o"),
                UpdatedAt = e.UpdatedAt.ToString("o")
            })
            .FirstOrDefaultAsync();

        if (entry == null) return NotFound();
        return Ok(entry);
    }

    [HttpPost]
    public async Task<ActionResult<EmotionEntryDto>> Create([FromBody] CreateEmotionEntryRequest req)
    {
        if (!DateOnly.TryParse(req.Date, out var date))
            return BadRequest(new { message = "Invalid date format. Use yyyy-MM-dd" });

        var category = await _db.Categories.FindAsync(req.CategoryId);
        if (category == null) return BadRequest(new { message = "Category not found" });

        var emotion = await _db.Emotions.FindAsync(req.EmotionId);
        if (emotion == null) return BadRequest(new { message = "Emotion not found" });

        var entry = new EmotionEntry
        {
            Date = date,
            Note = req.Note,
            UserId = UserId,
            CategoryId = req.CategoryId,
            EmotionId = req.EmotionId
        };

        _db.EmotionEntries.Add(entry);
        await _db.SaveChangesAsync();

        return CreatedAtAction(nameof(GetById), new { id = entry.Id }, new EmotionEntryDto
        {
            Id = entry.Id,
            Date = entry.Date.ToString("yyyy-MM-dd"),
            Note = entry.Note,
            UserId = entry.UserId,
            CategoryId = entry.CategoryId,
            EmotionId = entry.EmotionId,
            Category = new CategoryDto { Id = category.Id, Name = category.Name, Type = category.Type.ToString() },
            Emotion = new EmotionDto { Id = emotion.Id, Name = emotion.Name, Icon = emotion.Icon },
            CreatedAt = entry.CreatedAt.ToString("o"),
            UpdatedAt = entry.UpdatedAt.ToString("o")
        });
    }

    [HttpPut("{id}")]
    public async Task<ActionResult<EmotionEntryDto>> Update(int id, [FromBody] UpdateEmotionEntryRequest req)
    {
        var entry = await _db.EmotionEntries
            .Include(e => e.Category)
            .Include(e => e.Emotion)
            .FirstOrDefaultAsync(e => e.Id == id && e.UserId == UserId);

        if (entry == null) return NotFound();

        if (!DateOnly.TryParse(req.Date, out var date))
            return BadRequest(new { message = "Invalid date format. Use yyyy-MM-dd" });

        var category = await _db.Categories.FindAsync(req.CategoryId);
        if (category == null) return BadRequest(new { message = "Category not found" });

        var emotion = await _db.Emotions.FindAsync(req.EmotionId);
        if (emotion == null) return BadRequest(new { message = "Emotion not found" });

        entry.Date = date;
        entry.Note = req.Note;
        entry.CategoryId = req.CategoryId;
        entry.EmotionId = req.EmotionId;
        entry.UpdatedAt = DateTime.UtcNow;

        await _db.SaveChangesAsync();

        return Ok(new EmotionEntryDto
        {
            Id = entry.Id,
            Date = entry.Date.ToString("yyyy-MM-dd"),
            Note = entry.Note,
            UserId = entry.UserId,
            CategoryId = entry.CategoryId,
            EmotionId = entry.EmotionId,
            Category = new CategoryDto { Id = category.Id, Name = category.Name, Type = category.Type.ToString() },
            Emotion = new EmotionDto { Id = emotion.Id, Name = emotion.Name, Icon = emotion.Icon },
            CreatedAt = entry.CreatedAt.ToString("o"),
            UpdatedAt = entry.UpdatedAt.ToString("o")
        });
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> Delete(int id)
    {
        var entry = await _db.EmotionEntries
            .FirstOrDefaultAsync(e => e.Id == id && e.UserId == UserId);

        if (entry == null) return NotFound();

        _db.EmotionEntries.Remove(entry);
        await _db.SaveChangesAsync();

        return NoContent();
    }
}
