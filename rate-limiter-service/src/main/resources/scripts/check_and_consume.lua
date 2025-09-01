-- KEYS[1] is the unique key for the user (e.g., rate:limiter:192.168.1.1)
local user_key = KEYS[1]

-- ARGV[1] is the maximum number of tokens in the bucket (bucket_capacity)
local bucket_capacity = tonumber(ARGV[1])
-- ARGV[2] is the number of tokens to add per second (refill_rate_per_second)
local refill_rate_per_second = tonumber(ARGV[2])
-- ARGV[3] is the current time in seconds (unix timestamp)
local current_time = tonumber(ARGV[3])
-- ARGV[4] is the number of tokens this request costs
local requested_tokens = tonumber(ARGV[4])


local current_state = redis.call('HMGET', user_key, 'tokens', 'timestamp')
local last_tokens = tonumber(current_state[1])
local last_refill_time = tonumber(current_state[2])

-- If the user is new, initialize their bucket state
if last_tokens == nil then
    last_tokens = bucket_capacity
    last_refill_time = current_time
end

-- Refill the bucket with new tokens
local elapsed_time = math.max(0, current_time - last_refill_time)
local tokens_to_add = math.floor(elapsed_time * refill_rate_per_second)

local new_tokens = math.min(last_tokens + tokens_to_add, bucket_capacity)
local new_refill_time = current_time

local allowed = 0 -- 0 means deny
-- If there are enough tokens, consume them and allow the request
if new_tokens >= requested_tokens then
    new_tokens = new_tokens - requested_tokens
    allowed = 1 -- 1 means allow
end

redis.call('HMSET', user_key, 'tokens', new_tokens, 'timestamp', new_refill_time)
-- Set the key to expire after the bucket could have fully refilled, plus a buffer
local expire_time = math.ceil(bucket_capacity / refill_rate_per_second) + 60
redis.call('EXPIRE', user_key, expire_time)

-- Return 1 if allowed, 0 if denied
return allowed