-- Sample complaints seed script
USE Javasem;

INSERT INTO grievance (description, location, status, created_at, category_id, citizen_id) VALUES
('Deep pothole causing vehicle damage near Anna Nagar 2nd Avenue', 'Anna Nagar, Block B', 'OPEN', NOW(), 1, 1),
('Underground water pipe burst leaking thousands of liters', 'MG Road junction', 'OPEN', NOW(), 2, 1),
('Garbage bin overflowing and spreading onto main road', 'Gandhi Market, Stall 4', 'OPEN', NOW(), 3, 1),
('Streetlights not working on entire 4th Cross Street', '4th Cross, Indiranagar', 'OPEN', NOW(), 4, 1),
('Open manhole left uncovered after drainage maintenance', 'Koramangala 5th Block', 'OPEN', NOW(), 2, 1),
('Broken footpath slabs causing pedestrian tripping hazard', 'Station Road, West Gate', 'OPEN', NOW(), 1, 1),
('Uncollected solid waste attracting stray animals', 'Nehru Nagar Main Road', 'OPEN', NOW(), 3, 1),
('Flickering high-mast lamp creating driving hazard at night', 'Ring Road Flyover Entry', 'OPEN', NOW(), 4, 1),
('Low water pressure for last 4 days in residential colony', 'Green Glen Layout', 'OPEN', NOW(), 2, 1),
('Severe waterlogging due to blocked storm drain', 'Church Street lane 3', 'OPEN', NOW(), 1, 1),
('Dumping of construction debris blocking sidewalk', 'Outer Ring Road service lane', 'OPEN', NOW(), 3, 1),
('Damaged electric pole tilting dangerously towards road', 'Sector 14, Near Park', 'OPEN', NOW(), 4, 1),
('Sewage backup overflowing into residential area', 'Shanti Nagar 2nd Cross', 'OPEN', NOW(), 2, 1),
('Sinkhole forming in the middle of asphalt road', 'Commercial Street crossing', 'OPEN', NOW(), 1, 1),
('Foul odor from plastic and bio-waste accumulated near lake', 'Bellandur Lake bund road', 'OPEN', NOW(), 3, 1),
('Hanging live wires exposed from street distribution box', 'Residency Road corner', 'OPEN', NOW(), 4, 1),
('Contaminated turbid tap water supply in apartment cluster', 'Whitefield Main Rd', 'OPEN', NOW(), 2, 1),
('Speed breaker not painted and missing warning sign', 'Airport Bypass Road', 'OPEN', NOW(), 1, 1),
('Illegal trash dumping near government primary school', 'Vivekananda Nagar', 'OPEN', NOW(), 3, 1),
('Dark stretch due to burnt bulbs across whole pedestrian path', 'Lalbagh West Gate road', 'OPEN', NOW(), 4, 1);

-- To test SLA auto-escalation:
-- 1. Backdate complaints past SLA (e.g. by 10 days):
-- UPDATE grievance SET created_at = NOW() - INTERVAL 10 DAY WHERE id IN (11, 12, 13, 14, 15);
-- 2. Trigger SLA via POST: http://localhost:8080/api/grievances/test/trigger-sla
-- 3. Check Escalated Complaints: http://localhost:8080/api/grievances/escalated
