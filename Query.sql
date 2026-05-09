ALTER TABLE groups
    ADD COLUMN schedule_type VARCHAR(50);

UPDATE groups
SET schedule_type = 'EXACT_DAYS'
WHERE schedule_type IS NULL;

UPDATE groups
SET schedule_days = 'MONDAY,WEDNESDAY,FRIDAY'
WHERE schedule_type = 'EXACT_DAYS'
  AND schedule_days IS NULL;

select * from online_courses;
select * from online_course_lessons;
