namespace FeelJournal.DTOs;

public class MoodAnalysisDto
{
    public List<EmotionFrequencyDto> EmotionFrequencies { get; set; } = new();
    public List<CategoryMoodDto> CategoryMoods { get; set; } = new();
    public Dictionary<string, int> EmotionByCategory { get; set; } = new();
}

public class EmotionFrequencyDto
{
    public string EmotionName { get; set; } = string.Empty;
    public string EmotionIcon { get; set; } = string.Empty;
    public int Count { get; set; }
    public double Percentage { get; set; }
}

public class CategoryMoodDto
{
    public string CategoryName { get; set; } = string.Empty;
    public string CategoryType { get; set; } = string.Empty;
    public string DominantEmotion { get; set; } = string.Empty;
    public string DominantEmotionIcon { get; set; } = string.Empty;
    public int DominantEmotionCount { get; set; }
    public int TotalEntries { get; set; }
}

public class ExportRequest
{
    public string Format { get; set; } = "CSV";
    public string? DateFrom { get; set; }
    public string? DateTo { get; set; }
}
