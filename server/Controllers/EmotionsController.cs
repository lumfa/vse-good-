using FeelJournal.Data;
using FeelJournal.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/emotions")]
[Authorize]
public class EmotionsController : ControllerBase
{
    private readonly AppDbContext _db;

    public EmotionsController(AppDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<List<EmotionDto>>> GetAll()
    {
        var emotions = await _db.Emotions
            .OrderBy(e => e.Name)
            .Select(e => new EmotionDto
            {
                Id = e.Id,
                Name = e.Name,
                Icon = e.Icon
            })
            .ToListAsync();

        return Ok(emotions);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<EmotionDto>> GetById(int id)
    {
        var emotion = await _db.Emotions
            .Where(e => e.Id == id)
            .Select(e => new EmotionDto
            {
                Id = e.Id,
                Name = e.Name,
                Icon = e.Icon
            })
            .FirstOrDefaultAsync();

        if (emotion == null) return NotFound();
        return Ok(emotion);
    }
}
