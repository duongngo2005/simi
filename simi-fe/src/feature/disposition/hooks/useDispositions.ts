import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { dispositionApi } from "../api/dispositionApi";
import type {
  ItemDispositionFilter,
  ProcessItemDispositionsRequest,
} from "../types/disposition.type";

export const useItemDispositions = (filter: ItemDispositionFilter) => {
  return useQuery({
    queryKey: ["staff-dispositions", filter],
    queryFn: () => dispositionApi.searchDispositions(filter),
    staleTime: 15000,
  });
};

export const useConfirmReturn = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ProcessItemDispositionsRequest) =>
      dispositionApi.confirmReturn(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff-dispositions"] });
      queryClient.invalidateQueries({ queryKey: ["staff-dashboard"] });
    },
  });
};

export const useConfirmDonation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ProcessItemDispositionsRequest) =>
      dispositionApi.confirmDonation(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff-dispositions"] });
      queryClient.invalidateQueries({ queryKey: ["staff-dashboard"] });
    },
  });
};