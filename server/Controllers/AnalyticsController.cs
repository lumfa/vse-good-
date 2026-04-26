using System.Security.Claims;
using System.Text;
using FeelJournal.Data;
using FeelJournal.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FeelJournal.Controllers;

[ApiController]
[Route("api/analytics")]
[Authorize]
public class AnalyticsController : ControllerBase
{
    private readonly AppDbContext _db;

    public AnalyticsController(AppDbContext db) => _db = db;

    private int UserId => int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);

    [HttpGet("mood")]
    public async Task<ActionResult<MoodAnalysisDto>> AnalyzeMood(
        [FromQuery] string? dateFrom = null,
        [FromQuery] string? dateTo = null)
    {
        var query = _db.EmotionEntries
            .Include(e => e.Emotion)
            .Include(e => e.Category)
            .Where(e => e.UserId == UserId)
            .AsQueryable();

        if (dateFrom != null && DateOnly.TryParse(dateFrom, out var df))
            query = query.Where(e => e.Date >= df);

        if (dateTo != null && DateOnly.TryParse(dateTo, out var dt))
            query = query.Where(e => e.Date <= dt);

        var entries = await query.ToListAsync();

        if (entries.Count == 0)
            return Ok(new MoodAnalysisDto());

        var totalEntries = entries.Count;

        var emotionFrequencies = entries
            .GroupBy(e => e.EmotionId)
            .Select(g =>
            {
                var first = g.First();
                return new EmotionFrequencyDto
                {
                    EmotionName = first.Emotion.Name,
                    EmotionIcon = first.Emotion.Icon,
                    Count = g.Count(),
                    Percentage = Math.Round(g.Count() * 100.0 / totalEntries, 1)
                };
            })
            .OrderByDescending(e => e.Count)
            .ToList();

        var categoryMoods = entries
            .GroupBy(e => e.CategoryId)
            .Select(g =>
            {
                var dominant = g.GroupBy(e => e.EmotionId)
                    .OrderByDescending(eg => eg.Count())
                    .First();

                var firstCat = g.First();
                return new CategoryMoodDto
                {
                    CategoryName = firstCat.Category.Name,
                    CategoryType = firstCat.Category.Type.ToString(),
                    DominantEmotion = dominant.First().Emotion.Name,
                    DominantEmotionIcon = dominant.First().Emotion.Icon,
                    DominantEmotionCount = dominant.Count(),
                    TotalEntries = g.Count()
                };
            })
            .OrderByDescending(c => c.TotalEntries)
            .ToList();

        var emotionByCategory = entries
            .GroupBy(e => $"{e.Category.Name}:{e.Emotion.Name}")
            .ToDictionary(
                g => g.Key,
                g => g.Count()
            );

        return Ok(new MoodAnalysisDto
        {
            EmotionFrequencies = emotionFrequencies,
            CategoryMoods = categoryMoods,
            EmotionByCategory = emotionByCategory
        });
    }

    [HttpGet("export")]
    public async Task<IActionResult> Export([FromQuery] string format = "CSV",
        [FromQuery] string? dateFrom = null, [FromQuery] string? dateTo = null)
    {
        var query = _db.EmotionEntries
            .Include(e => e.Emotion)
            .Include(e => e.Category)
            .Where(e => e.UserId == UserId)
            .AsQueryable();

        if (dateFrom != null && DateOnly.TryParse(dateFrom, out var df))
            query = query.Where(e => e.Date >= df);

        if (dateTo != null && DateOnly.TryParse(dateTo, out var dt))
            query = query.Where(e => e.Date <= dt);

        var entries = await query
            .OrderByDescending(e => e.Date)
            .Select(e => new
            {
                e.Date,
                e.Note,
                Category = e.Category.Name,
                CategoryType = e.Category.Type.ToString(),
                Emotion = e.Emotion.Name,
                EmotionIcon = e.Emotion.Icon,
                e.CreatedAt
            })
            .ToListAsync();

        if (format.Equals("CSV", StringComparison.OrdinalIgnoreCase))
        {
            var sb = new StringBuilder();
            sb.AppendLine("Date,Note,Category,CategoryType,Emotion,EmotionIcon,CreatedAt");
            foreach (var e in entries)
            {
                var note = e.Note?.Replace("\"", "\"\"") ?? "";
                sb.AppendLine($"{e.Date:yyyy-MM-dd},\"{note}\",{e.Category},{e.CategoryType},{e.Emotion},{e.EmotionIcon},{e.CreatedAt:o}");
            }
            var bytes = Encoding.UTF8.GetBytes(sb.ToString());
            return File(bytes, "text/csv", $"feel_journal_export_{DateTime.UtcNow:yyyyMMdd}.csv");
        }

        var pdfBytes = GeneratePdf(entries);
        return File(pdfBytes, "application/pdf", $"feel_journal_export_{DateTime.UtcNow:yyyyMMdd}.pdf");
    }

    private static byte[] GeneratePdf(IEnumerable<dynamic> entries)
    {
        var sb = new StringBuilder();
        sb.AppendLine("<html><head><meta charset='utf-8'>");
        sb.AppendLine("<style>body{font-family:Arial,sans-serif;}table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}th{background:#4CAF50;color:white;}</style>");
        sb.AppendLine("</head><body><h1>Feel Journal Export</h1><table>");
        sb.AppendLine("<tr><th>Date</th><th>Note</th><th>Category</th><th>Type</th><th>Emotion</th><th>Icon</th></tr>");

        foreach (var e in entries)
        {
            sb.AppendLine($"<tr><td>{e.Date:yyyy-MM-dd}</td><td>{e.Note ?? ""}</td><td>{e.Category}</td><td>{e.CategoryType}</td><td>{e.Emotion}</td><td>{e.EmotionIcon}</td></tr>");
        }

        sb.AppendLine("</table></body></html>");

        return Encoding.UTF8.GetBytes(sb.ToString());
    }
}
