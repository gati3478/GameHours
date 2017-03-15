package gh.datamodel;

public class GameplayTimes {
	public static final String ATTRIBUTE_NAME = "gameplaytimes";
	private Integer mainGameplay;
	private Integer extraGameplay;
	private Integer completeGameplay;
	private Integer averageCombined;

	/**
	 * Constructs new GameplayTimes object representing gameplay durations on
	 * different scenarios.
	 * 
	 * @param mainGameplay
	 *            Gameplay hours spent to just beat the game.
	 * @param extraGameplay
	 *            Gameplay hours spent to beat the game with additional
	 *            activities.
	 * @param completeGameplay
	 *            Gameplay hours spent to beat the game with all additional
	 *            activities.
	 */
	public GameplayTimes(Integer mainGameplay, Integer extraGameplay,
			Integer completeGameplay) {
		this.mainGameplay = mainGameplay;
		this.extraGameplay = extraGameplay;
		this.completeGameplay = completeGameplay;
		this.averageCombined = getAverageCombinedTime();
	}

	/**
	 * Returns gameplay hours spent to just beat the game.
	 * 
	 * @return Gameplay hours spent to just beat the game.
	 */
	public Integer getMainGameplayTime() {
		return mainGameplay;
	}

	/**
	 * Returns gameplay hours spent to beat the game with additional activities.
	 * 
	 * @return Gameplay hours spent to beat the game with additional activities.
	 */
	public Integer getExtraGameplayTime() {
		return extraGameplay;
	}

	/**
	 * Returns gameplay hours spent to beat the game with all additional
	 * activities.
	 * 
	 * @return Gameplay hours spent to beat the game with all additional
	 *         activities.
	 */
	public Integer getCompleteGameplayTime() {
		return completeGameplay;
	}

	/**
	 * Returns average time required to beat the game with mixed playstyle.
	 * 
	 * @return Average time required to beat the game with mixed playstyle.
	 */
	public Integer getAverageCombinedTime() {
		int count = 0;
		int hours = 0;
		if (mainGameplay != null && mainGameplay.intValue() != 0) {
			hours += mainGameplay.intValue();
			++count;
		}
		if (extraGameplay != null && extraGameplay.intValue() != 0) {
			hours += extraGameplay.intValue();
			++count;
		}
		if (completeGameplay != null && completeGameplay.intValue() != 0) {
			hours += completeGameplay.intValue();
			++count;
		}

		if (count != 0)
			hours /= count;
		return new Integer(hours);
	}

	/**
	 * Two GameplayTimes objects are equal if all of their gameplay durations
	 * are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		// standard equals() technique 1
		if (obj == this)
			return true;

		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof GameplayTimes))
			return false;

		GameplayTimes otherTimes = (GameplayTimes) obj;
		return otherTimes.getMainGameplayTime().equals(this.mainGameplay)
				&& otherTimes.getExtraGameplayTime().equals(this.extraGameplay)
				&& otherTimes.getCompleteGameplayTime().equals(
						this.completeGameplay);
	}

	/**
	 * Returns GameplayTimes' object's String representation in format "Main
	 * Story: <code>mainGameplay</code>; Extra: <code>extraGameplay</code>;
	 * Complete: <code>completeGameplay</code>; "Combined:
	 * <code>getAverageCombinedTime()"</code>;"
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Main Story: " + mainGameplay + "; ");
		result.append("Extra: " + extraGameplay + "; ");
		result.append("Complete: " + completeGameplay + "; ");
		result.append("Combined: " + averageCombined + ";");
		return result.toString();
	}

}
