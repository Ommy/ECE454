def check(ours, theirs)
  ours.each do |k,v|
    keySplit = k.to_s.split(",")
    possibleKey = "#{keySplit[1]},#{keySplit[0]}"
    floatV = (v.to_f * 100).floor / 100.0
    if !theirs[k].nil? && !theirs[k].empty?
      if theirs[k].to_f.eql? floatV
        # puts "The samples #{k} match!"
      else
        puts "The samples for key #{k} don't match! Ours: #{v}, Theirs: #{theirs[k]}"
      end
    elsif !theirs[possibleKey.to_sym].nil? && !theirs[possibleKey.to_sym].empty?
      if theirs[possibleKey.to_sym].to_f.eql? floatV
        # puts "The samples #{k} match!"
      else
        puts "The samples for key #{k} don't match! Ours: #{v}, Theirs: #{theirs[possibleKey.to_sym]}"
      end
    else
      puts "Something is wrong with key #{key} or #{possibleKey}"
    end
  end
end


our_file = File.new(ARGV[0], "r")
their_file = File.new(ARGV[1], "r")

our_output = {}
their_output = {}

while (line = our_file.gets)
  str = line.split(",")
  temp = str[1]
  str.delete_at(1)
  str << temp.split(" ")
  str = str.flatten
  our_output["#{str[0]},#{str[1]}".to_sym] = str[2]
end

while (line = their_file.gets)
  str = line.split(",")
  their_output["#{str[0]},#{str[1]}".to_sym] = str[2]
end

check(our_output, their_output)