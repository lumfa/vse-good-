using FeelJournal.Data;
using FeelJournal.DTOs;
using FeelJournal.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/categories")]
[Authorize]
public class CategoriesController : ControllerBase
{
    private readonly AppDbContext _db;

    public CategoriesController(AppDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<List<CategoryDto>>> GetAll()
    {
        var categories = await _db.Categories
            .OrderBy(c => c.Name)
            .Select(c => new CategoryDto
            {
                Id = c.Id,
                Name = c.Name,
                Type = c.Type.ToString()
            })
            .ToListAsync();

        return Ok(categories);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<CategoryDto>> GetById(int id)
    {
        var category = await _db.Categories
            .Where(c => c.Id == id)
            .Select(c => new CategoryDto
            {
                Id = c.Id,
                Name = c.Name,
                Type = c.Type.ToString()
            })
            .FirstOrDefaultAsync();

        if (category == null) return NotFound();
        return Ok(category);
    }

    [HttpPost]
    public async Task<ActionResult<CategoryDto>> Create([FromBody] CreateCategoryRequest req)
    {
        if (!Enum.TryParse<CategoryType>(req.Type, true, out var type))
            return BadRequest(new { message = "Invalid category type. Use POSITIVE, NEGATIVE, or NEUTRAL" });

        var category = new Category { Name = req.Name, Type = type };

        _db.Categories.Add(category);
        await _db.SaveChangesAsync();

        return CreatedAtAction(nameof(GetById), new { id = category.Id },
            new CategoryDto { Id = category.Id, Name = category.Name, Type = category.Type.ToString() });
    }

    [HttpPut("{id}")]
    public async Task<ActionResult<CategoryDto>> Update(int id, [FromBody] UpdateCategoryRequest req)
    {
        var category = await _db.Categories.FindAsync(id);
        if (category == null) return NotFound();

        if (!Enum.TryParse<CategoryType>(req.Type, true, out var type))
            return BadRequest(new { message = "Invalid category type" });

        category.Name = req.Name;
        category.Type = type;
        await _db.SaveChangesAsync();

        return Ok(new CategoryDto { Id = category.Id, Name = category.Name, Type = category.Type.ToString() });
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> Delete(int id)
    {
        var hasEntries = await _db.EmotionEntries.AnyAsync(e => e.CategoryId == id);
        if (hasEntries) return Conflict(new { message = "Cannot delete category with existing entries" });

        var category = await _db.Categories.FindAsync(id);
        if (category == null) return NotFound();

        _db.Categories.Remove(category);
        await _db.SaveChangesAsync();

        return NoContent();
    }
}
