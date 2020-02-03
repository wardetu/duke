package duke.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

class DeadlineTask extends Task {
    protected LocalDateTime time;

    public DeadlineTask(String[] inputArr) throws Exception {
        this.type = "deadline";
        if (inputArr.length < 2) {
            throw new Exception("Deadline tasks must have a non-empty description!");
        }
        this.description = Arrays.stream(inputArr)
                .map(str -> str.toLowerCase().equals("deadline") ? "" : str.equals("/by") ? "by" : str)
                .collect(Collectors.joining(" "));
        int lastBy = description.lastIndexOf(" by ");
        if (lastBy == -1) {
            throw new Exception("Keyword \"by\" missing");
        } else {
            try {
                this.time = LocalDateTime.parse(this.description.substring(lastBy + 4),
                        DateTimeFormatter.ofPattern("yyyy-LL-dd HHmm"));

            } catch (DateTimeParseException e) {
                try {
                    this.time = LocalDateTime.parse(this.description.substring(lastBy + 4),
                            DateTimeFormatter.ofPattern("MMM d yyyy hh:mma"));
                } catch (DateTimeParseException e2) {
                    throw new Exception("Error: unable to decipher date & time input.");
                }
            }
            this.description = this.description.substring(0, lastBy + 3) + ' '
                    + time.format(DateTimeFormatter.ofPattern("MMM d yyyy hh:mma"));
        }
    }

    @Override
    public String toString() {
        return " DEADLINE" + super.toString();
    }
}